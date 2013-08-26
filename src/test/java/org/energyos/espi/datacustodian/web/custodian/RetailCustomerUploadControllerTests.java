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

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class RetailCustomerUploadControllerTests {

    private RetailCustomerService retailCustomerService;
    private UsagePointService usagePointService;
    private RetailCustomerUploadController controller;
    private BindingResult result;
    private UploadForm form;

    @Before
    public void before() {
        controller = new RetailCustomerUploadController();

        RetailCustomer retailCustomer = new RetailCustomer();

        retailCustomerService = mock(RetailCustomerService.class);
        when(retailCustomerService.findById(anyLong())).thenReturn(retailCustomer);
        controller.setRetailCustomerService(retailCustomerService);

        usagePointService = mock(UsagePointService.class);
        controller.setUsagePointService(usagePointService);

        form = new UploadForm();
        form.setFile(mock(MultipartFile.class));

        result = mock(BindingResult.class);
    }

    @Test
    public void upload_displaysRetailCustomerUploadView() throws Exception {
        ModelMap model = new ModelMap();

        assertEquals("/custodian/retailcustomers/upload", controller.upload(1L, model));
    }

    @Test
    public void upload_setsRetailCustomerModel() throws Exception {
        ModelMap model = new ModelMap();

        controller.upload(1L, model);

        assertNotNull(model.get("retailCustomer"));
    }

    @Test
    public void uploadPost_givenValidFile_importsUsagePointWithNoErrors() throws Exception {
        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setId(1L);
        when(retailCustomerService.findById(anyLong())).thenReturn(retailCustomer);

        String view = controller.uploadPost(retailCustomer.getId(), form, result);

        verify(usagePointService).importUsagePoint(any(RetailCustomer.class), any(InputStream.class));
        assertEquals(false, result.hasErrors());
        assertEquals("redirect:/custodian/retailcustomers/1/show", view);
    }

    @Test
    public void uploadPost_givenInvalidFile_displaysHomeViewWithErrors() throws Exception {
        Mockito.doThrow(new JAXBException("Unable to process file")).when(usagePointService).importUsagePoint(any(RetailCustomer.class), any(InputStream.class));
        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setId(1L);
        when(retailCustomerService.findById(anyLong())).thenReturn(retailCustomer);

        String view = controller.uploadPost(retailCustomer.getId(), form, result);

        assertEquals("/custodian/retailcustomers/upload", view);
        verify(result).addError(new ObjectError("uploadForm", "Unable to process file"));
    }

}
