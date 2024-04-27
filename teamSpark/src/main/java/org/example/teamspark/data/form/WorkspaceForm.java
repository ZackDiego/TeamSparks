package org.example.teamspark.data.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceForm {
    private String name;
    private MultipartFile avatarImageFile;
}
