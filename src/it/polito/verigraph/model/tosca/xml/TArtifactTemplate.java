//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.11.14 alle 10:51:41 AM CET 
//


package it.polito.verigraph.model.tosca.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per tArtifactTemplate complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="tArtifactTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityTemplate">
 *       &lt;sequence>
 *         &lt;element name="ArtifactReferences" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ArtifactReference" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactReference" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifactTemplate", propOrder = {
    "artifactReferences"
})
public class TArtifactTemplate
    extends TEntityTemplate
{

    @XmlElement(name = "ArtifactReferences")
    protected TArtifactTemplate.ArtifactReferences artifactReferences;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Recupera il valore della proprietà artifactReferences.
     * 
     * @return
     *     possible object is
     *     {@link TArtifactTemplate.ArtifactReferences }
     *     
     */
    public TArtifactTemplate.ArtifactReferences getArtifactReferences() {
        return artifactReferences;
    }

    /**
     * Imposta il valore della proprietà artifactReferences.
     * 
     * @param value
     *     allowed object is
     *     {@link TArtifactTemplate.ArtifactReferences }
     *     
     */
    public void setArtifactReferences(TArtifactTemplate.ArtifactReferences value) {
        this.artifactReferences = value;
    }

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ArtifactReference" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tArtifactReference" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "artifactReference"
    })
    public static class ArtifactReferences {

        @XmlElement(name = "ArtifactReference", required = true)
        protected List<TArtifactReference> artifactReference;

        /**
         * Gets the value of the artifactReference property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the artifactReference property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getArtifactReference().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TArtifactReference }
         * 
         * 
         */
        public List<TArtifactReference> getArtifactReference() {
            if (artifactReference == null) {
                artifactReference = new ArrayList<TArtifactReference>();
            }
            return this.artifactReference;
        }

    }

}
