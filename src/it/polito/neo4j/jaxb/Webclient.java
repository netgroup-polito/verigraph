//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.07.03 alle 04:31:00 PM CEST 
//


package it.polito.neo4j.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="nameWebServer" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "webclient")
public class Webclient {

    @XmlAttribute(name = "nameWebServer", required = true)
    protected String nameWebServer;

    /**
     * Recupera il valore della propriet� nameWebServer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameWebServer() {
        return nameWebServer;
    }

    /**
     * Imposta il valore della propriet� nameWebServer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameWebServer(String value) {
        this.nameWebServer = value;
    }

}