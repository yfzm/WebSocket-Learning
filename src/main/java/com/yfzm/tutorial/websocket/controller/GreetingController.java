package com.yfzm.tutorial.websocket.controller;

import com.yfzm.tutorial.websocket.dto.InMessage;
import com.yfzm.tutorial.websocket.dto.OutMessage;
import com.yfzm.tutorial.websocket.util.SocketSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class GreetingController {

    @Autowired
    SocketSessionRegistry webAgentSessionRegistry;
    /**
     * 消息发送工具
     */
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/change-notice")
    @SendTo("/topic/notice")
    public String greeting(String value) {
        return value;
    }

    @GetMapping(value = "/msg/sendcommuser")
    public @ResponseBody
    OutMessage SendToCommUserMessage(HttpServletRequest request) {
        List<String> keys = webAgentSessionRegistry.getAllSessionIds().entrySet()
                .stream().map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Date date = new Date();
        keys.forEach(x -> {
            String sessionId = webAgentSessionRegistry.getSessionIds(x).stream().findFirst().get().toString();
            template.convertAndSendToUser(sessionId, "/topic/greetings", new OutMessage("commmsg：allsend, " + "send  comm" + date.getTime() + "!"), createHeaders(sessionId));

        });
        return new OutMessage("sendcommuser, " + new Date() + "!");
    }


    /**
     * 同样的发送消息   只不过是ws版本  http请求不能访问
     * 根据用户key发送消息
     *
     * @param message
     * @return
     * @throws Exception
     */
    @MessageMapping("/msg/hellosingle")
    public void greeting2(InMessage message) throws Exception {
        Map<String, String> params = new HashMap(1);
        params.put("test", "test");
        //这里没做校验

        for (String sessionId: webAgentSessionRegistry.getSessionIds(message.getReceiver())) {
            template.convertAndSendToUser(sessionId, "/topic/greetings", new OutMessage(message.getSender() + ": " + message.getMsg()), createHeaders(sessionId));
        }

//        String sessionId = webAgentSessionRegistry.getSessionIds(message.getReceiver()).stream().findFirst().get();
//        template.convertAndSendToUser(sessionId, "/topic/greetings", new OutMessage(message.getSender() + ": " + message.getMsg()), createHeaders(sessionId));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
