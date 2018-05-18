package com.rmadegps.rmadeservermanagement.controller;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class Spring404error implements EmbeddedServletContainerCustomizer
{

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container)
    {
        container.addErrorPages( new ErrorPage( HttpStatus.NOT_FOUND, "/restapi/rpt/app/404.html" ) );
    }
}
