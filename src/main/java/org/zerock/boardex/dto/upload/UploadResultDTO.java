package org.zerock.boardex.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    private String uuid;
    private String fileName;
    private boolean img;
    public String getLink(){
        if(img){
            //img가 true 값이라면 파일 이름에 s_uuid_파일이름 반환
            return "s_"+uuid+"_"+fileName;
        }else {
            //img가 false 값이라면 uuid_파일이름
            return uuid+"_"+fileName;
        }
    }
}
