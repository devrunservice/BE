package com.devrun.util;

import com.google.common.net.HttpHeaders;
  import javax.servlet.*;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.util.Collection;


  public class CookieAttributeFilter implements Filter{
		
		
		@Override
	        public void doFilter(ServletRequest request, ServletResponse response,
	                FilterChain chain) throws IOException, ServletException {
			
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			chain.doFilter(request, response);
			addSameSite(httpServletResponse); 
			
		}
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			// TODO Auto-generated method stub
		}
		@Override
		public void destroy() {
			// TODO Auto-generated method stub	
		}	
		
		private void addSameSite(HttpServletResponse response) {
	        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
	        for (String header : headers) {
	            response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; %s", header, "SameSite=None; Secure"));
	        }
	        
	    }
		
	}