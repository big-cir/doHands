package com.be.dohands.member.dto;

import java.util.Base64;
import lombok.Getter;

@Getter
public class MultiCursor {
    private String evaluationExpCursor;
    private String leaderQuestExpCursor;
    private String tfExpCursor;
    private String jobQuestExpCursor;

    public MultiCursor(String evaluationExpCursor, String leaderQuestExpCursor, String tfExpCursor,
                       String jobQuestExpCursor) {
        this.evaluationExpCursor = evaluationExpCursor;
        this.leaderQuestExpCursor = leaderQuestExpCursor;
        this.tfExpCursor = tfExpCursor;
        this.jobQuestExpCursor = jobQuestExpCursor;
    }

    public void updateCursor(String type, String cursor) {
        if (type.equals("evaluation")) this.evaluationExpCursor = cursor;
        else if (type.equals("leader")) this.leaderQuestExpCursor = cursor;
        else if (type.equals("tf")) this.tfExpCursor = cursor;
        else if (type.equals("job")) this.jobQuestExpCursor = cursor;
    }

    public String serialize() {
        String cursor = String.join(",",
                evaluationExpCursor != null ? evaluationExpCursor : " ",
                leaderQuestExpCursor != null ? leaderQuestExpCursor : " ",
                tfExpCursor != null ? tfExpCursor : " ",
                jobQuestExpCursor != null ? jobQuestExpCursor : " "
        );
        return Base64.getEncoder().encodeToString(cursor.getBytes());
    }

    public static MultiCursor deserialize(String encodeCursor) {
        if (encodeCursor.isEmpty() || encodeCursor == null) {
            return new MultiCursor(null, null, null, null);
        }

        String decoded = new String(Base64.getDecoder().decode(encodeCursor));
        String[] cursors = decoded.split(",");

        return new MultiCursor(
                cursors[0].isEmpty() ? null : cursors[0],
                cursors[1].isEmpty() ? null : cursors[1],
                cursors[2].isEmpty() ? null : cursors[2],
                cursors[3].isEmpty() ? null : cursors[3]
        );
    }
}
