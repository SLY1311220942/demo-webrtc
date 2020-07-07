package com.sly.demo.webrtc.service;


import com.sly.demo.webrtc.config.OneReadyResponseConfig;
import com.sly.demo.webrtc.storage.FiveHomeStorage;
import com.sly.demo.webrtc.storage.OneReadyStorage;
import com.sly.demo.webrtc.storage.PersonalSessionStorage;

import javax.websocket.Session;

/**
 * 不同消息逻辑处理层
 *
 * @author SLY
 */
public class SocketService {

    /**
     * 搜索个人 是否可以对话
     *
     * @return
     */
    public static boolean queryId(String thisId, String id) {
        if (!thisId.equals(id)) {
            //不可以搜索自己
            Session session = PersonalSessionStorage.getSessionById(id);
            if (session == null) {
                return false;
            }
            //查询它是否正在一对一对话
            if (OneReadyStorage.isReady(id)) {
                return false;
            }
            //查询它是否正在群组对话
            if (FiveHomeStorage.isReady(id)) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 发送一对一对话请求
     *
     * @param thisId
     * @param id
     * @return
     */
    public static String readyForOne(String thisId, String id) {
        if (OneReadyStorage.isReady(thisId)) {
            //自己已经准备过,取消准备
            OneReadyStorage.delCall(thisId);
        } else {
            //再次检测对方是否可以对话,结果为可发起对话
            if (SocketService.queryId(thisId, id)) {
                //添加发起准备的信息
                OneReadyStorage.addCall(thisId, id);
                return id;
            }
        }
        return null;
    }

    /**
     * 一对一准备响应
     * 返回需要发送的session
     * 很可能会发回被呼叫方,因为呼叫方取消了准备
     *
     * @param thisId
     * @param is
     * @return
     */
    public static OneReadyResponseConfig readyForOneResponse(String thisId, boolean is) {
        OneReadyResponseConfig map = new OneReadyResponseConfig();
        String callId = OneReadyStorage.answerOkReady(thisId);
        //呼叫方取消了准备
        if (callId == null) {
            map.setMsg("呼叫方取消了准备");
            map.setStatus(false);
            map.setOfferId(thisId);
            return map;
        }
        map.setMsg("对方" + (is ? "同意" : "拒绝") + "对话");
        map.setStatus(is);
        map.setAnswerId(thisId);
        map.setOfferId(callId);
        return map;
    }

    /**
     * 发送信令给对方,这里不区分是群组 还是个人,还是呼叫方或者是应答方,总之会找到对方并发给它
     *
     * @param thisId
     * @return
     */
    public static OneReadyResponseConfig signallingOffer(String thisId) {
        OneReadyResponseConfig map = new OneReadyResponseConfig();
        //先找到当前发送者与谁建立了对话关系
        String answerId = OneReadyStorage.findReadyAnswerBycallId(thisId);
        //非法发送信令,根本不存在通话准备信息
        if (answerId == null) {
            if (answerId == null) {
                map.setMsg("非法发送信令,根本不存在通话准备信息");
                map.setStatus(false);
                map.setAnswerId(thisId);
                return map;
            }
        }
        map.setStatus(true);
        map.setAnswerId(answerId);
        return map;
    }

    /**
     * 关闭双方的对话准备,并且返回对方的id
     *
     * @param thisId
     * @return
     */
    public static String oneChannelClose(String thisId) {
        String answerId = OneReadyStorage.findReadyAnswerBycallId(thisId);
        if (answerId == null) {
            return null;
        }
        //删掉双方的对话准备
        OneReadyStorage.readyClose(thisId, answerId);
        return answerId;
    }

    /**
     * 创建房间
     *
     * @return
     */
    public static String createHome() {
        return FiveHomeStorage.createHome();
    }

    /**
     * 用户加入一个房间
     *
     * @param userId
     * @param homeId
     */
    public static boolean addHome(String userId, String homeId) {
        return FiveHomeStorage.addHome(userId, homeId);
    }

    /**
     * 用户退出房间
     * 如果退出后房间里面没有人了,则删除房间
     *
     * @param userId
     */
    public static void exitHome(String userId) {
        FiveHomeStorage.exitHome(userId);
    }
}
