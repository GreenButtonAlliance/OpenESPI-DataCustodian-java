/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.web.custodian;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;

import org.energyos.espi.common.service.ImportService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

public class UploadControllerTests {

    private ImportService importService;
    private UploadController controller;
    private BindingResult result;
    private UploadForm form;

    @Before
    public void before() {
        controller = new UploadController();

        importService = mock(ImportService.class);
        controller.setImportService(importService);

        form = new UploadForm();
        form.setFile(mock(MultipartFile.class));

        result = mock(BindingResult.class);
    }

    @Test
    public void upload_displaysUploadView() throws Exception {
        assertEquals("/custodian/upload", controller.upload());
    }

    @Test
    @Ignore
    public void uploadPost_givenValidFile_importsUsagePointWithNoErrors() throws Exception {
        String view = controller.uploadPost(form, result);

        verify(importService).importData(any(InputStream.class), null);
        assertEquals(false, result.hasErrors());
        assertEquals("redirect:/custodian/retailcustomers", view);
    }

    @Test
    @Ignore
    public void uploadPost_givenInvalidFile_displaysUploadViewWithErrors() throws Exception {
        Mockito.doThrow(new SAXException("Unable to process file")).when(importService).importData(any(InputStream.class), null);

        String view = controller.uploadPost(form, result);

        assertEquals("/custodian/upload", view);
        verify(result).addError(new ObjectError("uploadForm", "Unable to process file"));
    }
}
