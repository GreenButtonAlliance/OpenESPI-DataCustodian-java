package org.energyos.espi.datacustodian.utils.factories;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FixtureFactory {
    private FixtureFactory() {}

    public static InputStream newUsagePointInputStream(UUID uuid) throws IOException {
        return new ByteArrayInputStream(newUsagePointXML(uuid).getBytes());
    }

    public static String newUsagePointXML(UUID uuid) throws IOException {
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/test_usage_data.xml");
        String xml = FileUtils.readFileToString(sourceFile.getFile());
        xml = xml.replaceFirst("7BC41774-7190-4864-841C-861AC76D46C2", uuid.toString());
        return xml;
    }
}
