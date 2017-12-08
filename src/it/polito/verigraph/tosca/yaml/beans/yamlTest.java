package it.polito.verigraph.tosca.yaml.beans;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.polito.verigraph.tosca.YamlParsingUtils;

public class yamlTest {
	
    public static void main(String[] args) {
        
        try {
            ServiceTemplateYaml service = YamlParsingUtils.obtainServiceTemplate("D:\\GIT_repository\\verigraph\\tosca_examples\\DummyServiceTemplate.yaml");
            
            Map<String,NodeTemplateYaml> nodes = YamlParsingUtils.obtainNodeTemplates(service);
            System.out.println("boom");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

