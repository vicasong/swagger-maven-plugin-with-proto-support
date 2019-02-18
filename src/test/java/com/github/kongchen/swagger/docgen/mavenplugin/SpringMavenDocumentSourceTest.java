package com.github.kongchen.swagger.docgen.mavenplugin;

import me.vica.swagger.docgen.ProtoBufferedModelResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.converter.ModelConverters;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import me.vicasong.swagger.docgen.protocol.TestProtos;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;

public class SpringMavenDocumentSourceTest
{
    @Test
    public void testGetValidClasses() throws Exception
    {
        Log log = new SystemStreamLog();

        ApiSource apiSource = new ApiSource();
        apiSource.setLocations(Collections.singletonList(this.getClass().getPackage().getName()));
        apiSource.setSwaggerDirectory("./");

        SpringMavenDocumentSource springMavenDocumentSource = new SpringMavenDocumentSource(apiSource, log, "UTF-8");

        Set<Class<?>> validClasses = springMavenDocumentSource.getValidClasses();

        Assert.assertEquals(validClasses.size(), 2);
        Assert.assertTrue(validClasses.contains(ExampleController1.class));
        Assert.assertTrue(validClasses.contains(ExampleController2.class));
    }


    @Test
    public void testReadProtobufModel() throws Exception
    {
        // add converters
        ModelConverters.getInstance().addConverter(new ProtoBufferedModelResolver(Json.mapper()));
        ApiSource apiSource = new ApiSource();
        apiSource.setLocations(Collections.singletonList(this.getClass().getPackage().getName()));
        apiSource.setSwaggerDirectory("./");

        Log log = new SystemStreamLog();
        SpringMavenDocumentSource springMavenDocumentSource = new SpringMavenDocumentSource(apiSource, log, "UTF-8");

        Set<Class<?>> validClasses = springMavenDocumentSource.getValidClasses();

        Swagger swagger = springMavenDocumentSource.resolveApiReader().read(validClasses);
        assert swagger.getDefinitions().containsKey("StudentMsg");
        assert swagger.getDefinitions().containsKey("BaseInfoMsg");
        assert swagger.getDefinitions().get("BaseInfoMsg").getProperties().size() == 5;
    }


    @RestController
    private static class ExampleController1
    {
    }

    @Api
    @RestController
    private static class ExampleController2
    {

        @RequestMapping(value = "proto-test")
        @ApiOperation(value = "Test Protobuf Model", response = TestProtos.StudentMsg.class)
        public TestProtos.StudentMsg testReturn(@RequestBody TestProtos.StudentMsg msg) {
            // nothing to do
            return msg;
        }
    }



}
