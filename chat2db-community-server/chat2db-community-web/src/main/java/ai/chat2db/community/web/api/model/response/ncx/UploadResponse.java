package ai.chat2db.community.web.api.model.response.ncx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private String result;

    private int count;
}
