package com.instrumentalid.iiq.plugin.sup;

import com.instrumentalid.iiq.plugin.sup.dto.SUPConfiguration;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import sailpoint.api.Identitizer;
import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Configuration;
import sailpoint.object.Identity;
import sailpoint.plugin.PluginContext;
import sailpoint.tools.BrandingService;
import sailpoint.tools.BrandingServiceFactory;
import sailpoint.tools.GeneralException;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TestRefreshService {
    private MockedStatic<BrandingServiceFactory> brandingServiceFactoryMock;

    @AfterEach
    public void tearDown() {
        brandingServiceFactoryMock.close();
    }

    @Test
    public void testFetchConfiguration() {
        var config = new Configuration();
        config.setName("SUP Configuration");

        List<Map<String, Object>> buttonsList = new ArrayList<>();
        Map<String, Object> button = new HashMap<>();
        button.put("label", "Test Button");
        button.put("type", "testType");
        button.put("icon", "testIcon");
        button.put("rightRequired", "testRight");
        button.put("refreshOptions", Map.of("option1", "value1"));
        buttonsList.add(button);

        config.put("Refresh Buttons", buttonsList);

        Set<String> testRights1 = Set.of("testRight");

        SUPConfiguration supConfig = SUPConfiguration.create(config, testRights1);

        Assertions.assertNotNull(supConfig);
        Assertions.assertNotNull(supConfig.getButtons());
        Assertions.assertEquals(1, supConfig.getButtons().size());

        var buttonOutput1 = supConfig.getButtons().get(0);
        Assertions.assertEquals("Test Button", buttonOutput1.getLabel());
        Assertions.assertEquals("testType", buttonOutput1.getType());
        Assertions.assertEquals("testIcon", buttonOutput1.getIcon());
        Assertions.assertEquals("value1", buttonOutput1.getOptions().get("option1"));

        Set<String> testRights2 = Set.of();

        SUPConfiguration supConfig2 = SUPConfiguration.create(config, testRights2);
        Assertions.assertNotNull(supConfig2);
        Assertions.assertNotNull(supConfig2.getButtons());
        Assertions.assertEquals(0, supConfig2.getButtons().size());
    }

    @Test
    public void testRefresh1() throws GeneralException {
        var config = new Configuration();
        config.setName("SUP Configuration");

        List<Map<String, Object>> buttonsList = new ArrayList<>();
        Map<String, Object> button = new HashMap<>();
        button.put("label", "Test Button");
        button.put("type", "testType");
        button.put("icon", "testIcon");
        button.put("rightRequired", "testRight");
        button.put("refreshOptions", Map.of(Identitizer.ARG_CHECK_HISTORY, "true"));
        buttonsList.add(button);

        config.put("Refresh Buttons", buttonsList);

        Set<String> rights = Set.of("testRight");

        SUPConfiguration supConfig = SUPConfiguration.create(config, rights);

        Identity target = new Identity();
        target.setName("hi");

        Identity loggedInUser = new Identity();
        loggedInUser.setName("spadmin");

        SailPointContext ctx = Mockito.mock(SailPointContext.class);
        PluginContext pluginContext = Mockito.mock(PluginContext.class);
        Mockito.when(pluginContext.getSettingString("configurationObject")).thenReturn("SUP Configuration");
        Mockito.when(ctx.getObject(Configuration.class, "SUP Configuration")).thenReturn(config);
        Mockito.when(ctx.getObject(Identity.class, "hi")).thenReturn(target);

        AtomicReference<Map<String, Object>> optionsPassedToRefresh = new AtomicReference<>();

        RefreshService refreshService = new RefreshService(ctx, loggedInUser, pluginContext) {
            @Override
            public SUPConfiguration getConfigurations() throws GeneralException {
                return supConfig;
            }

            @Override
            protected void auditRefresh(String type, Identity targetIdentity, Attributes<String, Object> options) {
                // Do nothing
            }

            @Override
            protected void refreshIdentityInternal(Identity target, Attributes<String, Object> options) throws GeneralException {
                optionsPassedToRefresh.set(options);
            }
        };

        refreshService.refreshIdentity("testType", "hi");

        Assertions.assertNotNull(optionsPassedToRefresh.get());

        Assertions.assertEquals("true", optionsPassedToRefresh.get().get(Identitizer.ARG_CHECK_HISTORY));
    }

    @BeforeEach
    public void testSetup() {
        BrandingService brandingService = Mockito.mock(BrandingService.class);
        Mockito.when(brandingService.getAdminUserName()).thenReturn("spadmin");
        brandingServiceFactoryMock = Mockito.mockStatic(BrandingServiceFactory.class);
        brandingServiceFactoryMock.when(BrandingServiceFactory::getService).thenReturn(brandingService);
    }
}
