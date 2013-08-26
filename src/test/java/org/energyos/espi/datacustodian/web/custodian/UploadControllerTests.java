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

import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UploadControllerTests {

    private UsagePointService service;
    private UploadController controller;
    private BindingResult result;
    private UploadForm form;

    @Before
    public void before() {
        service = mock(UsagePointService.class);
        form = new UploadForm();
        form.setFile(mock(MultipartFile.class));
        controller = new UploadController();
        controller.setService(service);
        result = mock(BindingResult.class);
    }

    @Test
    public void upload_displaysUsagePointUploadView() throws Exception {
        assertEquals("/custodian/upload", controller.upload());
    }

    @Test
    public void uploadPost_givenInvalidFile_displaysHomeViewWithErrors() throws Exception {
        Mockito.doThrow(new JAXBException("Unable to process file")).when(service).importUsagePoint(any(InputStream.class));

        String view = controller.uploadPost(form, result);

        assertEquals("/custodian/upload", view);
        verify(result).addError(new ObjectError("uploadForm", "Unable to process file"));
    }

    @Test
    public void uploadPost_givenValidFile_importsUsagePointWithNoErrors() throws Exception {
        controller.uploadPost(form, result);

        verify(service).importUsagePoint(form.getFile().getInputStream());
        assertEquals(false, result.hasErrors());
    }
}
