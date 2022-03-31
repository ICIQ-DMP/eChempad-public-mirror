package org.ICIQ.eChempad.configurations;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/*
 * https://stackoverflow.com/questions/53199558/hibernate-mapping-exception-could-not-determine-type-for-java-nio-file-path
 * To transparently convert Path objects to String from DB to memory and the opposite.
 *
 * "The Converter annotation has an element autoApply. If the value of this element is set to true (default is false),
 * the converter will be applied to all attributes of the target 'type', including to basic attribute values that are
 * contained within other, more complex attribute types. In this case we don't have to use @Convert annotation
 * explicitly on the attributes."
 */
@Converter(autoApply = true)  // With autoapply = true performs automatic translation between types implicitly
public class MultipartFileConverter implements AttributeConverter<MockMultipartFile, String>, Serializable {

    @Value("${file.upload-dir}")
    private String download_dir;
    // receives standardMultipartHttpServletRequest$StandardMultipartFile on crash
    @Override
    public String convertToDatabaseColumn(MockMultipartFile file) {
        if (file instanceof MockMultipartFile)
        {
            if (file == null || file.getOriginalFilename() == null)
            {
                return "null";
            }
            else
            {
                Logger.getGlobal().info("MARCAAAAtoDB"+ file.getName());
                return file.getName();
            }
        } else return null;
    }

    @Override
    public MockMultipartFile convertToEntityAttribute(String s) {
        Path path = Paths.get(this.download_dir + "/" + s);
        byte[] file_content = null;
        try {
            Logger.getGlobal().info("MARCAAAAToMEM"+ path.toString());
            file_content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return new MockMultipartFile(s, s, "application/octet-stream", file_content);
    }
}
