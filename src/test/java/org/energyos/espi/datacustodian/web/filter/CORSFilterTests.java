package org.energyos.espi.datacustodian.web.filter;

import org.energyos.espi.datacustodian.web.filter.CORSFilter;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CORSFilterTests {
    @Test
    public void testDoFilterInternal() throws Exception {
        CORSFilter corsFilter = new CORSFilter();
        FilterChain filterChain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        corsFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
