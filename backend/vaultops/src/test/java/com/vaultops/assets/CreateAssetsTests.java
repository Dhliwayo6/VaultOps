package com.vaultops.assets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.controllers.AssetController;
import com.vaultops.dtos.AssetDTO;
import com.vaultops.enums.Assignment;
import com.vaultops.enums.ConditionStatus;
import com.vaultops.enums.Usage;
import com.vaultops.model.Asset;
import com.vaultops.services.asset.CreateAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AssetController.class)
public class CreateAssetsTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CreateAssetService createAssetService;

    private Asset asset;
    private AssetDTO assetDTO;

    void setUp() {
        asset = new Asset();
        asset.setId(1L);
        asset.setName("Macbook laptop");
        asset.setType("Laptop");
        asset.setUsageStatus(Usage.IN_USE);
        asset.setConditionStatus(ConditionStatus.FAIR);
        asset.setAssignment(Assignment.ASSIGNED);

        assetDTO = new AssetDTO(asset);

    }

}
