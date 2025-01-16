package com.be.dohands.notification.data;

import java.util.HashMap;
import java.util.Map;

public enum NotificationType {
    ARTICLE("게시글 생성 알림", "새로운 게시글이 생성되었습니다.") {
        @Override
        public Map<String, String> getData(Object... obj) {
            Map<String, String> data = new HashMap<>();
            data.put("title", ARTICLE.title);
            data.put("content", ARTICLE.content);
            return data;
        }
    },
    EXP("경험치 획득 알림", " EXP를 획득했습니다.") {
        @Override
        public Map<String, String> getData(Object... obj) {
            int exp = 0;
            if (obj.length > 0 && obj[0] instanceof Integer) {
                exp = (int) obj[0];
            }

            Map<String, String> data = new HashMap<>();
            data.put("title", EXP.title);
            data.put("content", exp + EXP.content);
            return data;
        }
    };

    private final String title;
    private final String content;

    NotificationType(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public abstract Map<String, String> getData(Object... obj);
}
