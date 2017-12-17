package it.polito.verigraph.tosca.yaml.beans;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.polito.verigraph.tosca.YamlParsingUtils;

public class yamlTest {
	
    public static void main(String[] args) {
        
        try {
            ServiceTemplateYaml service = YamlParsingUtils.obtainServiceTemplate("./tosca_examples/SimpleTemplate.yaml");
            
            Map<String,NodeTemplateYaml> nodes = YamlParsingUtils.obtainNodeTemplates(service);
            YamlParsingUtils.obtainConfiguration(nodes.get("100"));
            System.out.println("boom");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

