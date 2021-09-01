package com.github.axthosarouris.xmlreport;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ClassEntry {

    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "sourcefilename")
    private String sourceFilename;
    @XmlElement(name = "method")
    private List<MethodEntry> methodEntries;

    public List<MethodEntry> getMethodEntries() {
        return methodEntries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }
}
