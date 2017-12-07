package it.polito.verigraph.tosca.yaml.beans;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class yamlTest {
	
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            ServiceTemplateYaml service = mapper.readValue(new File("toscaDummyTemplate.yaml"), ServiceTemplateYaml.class);
            System.out.println("palla");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}