package com.github.axthosarouris.xmlreport;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class PackageEntry {

    @XmlAttribute(name = "name")
    private String name;
    @XmlElement(name = "class")
    private List<ClassEntry> classEntries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClassEntry> getClassEntries() {
        return classEntries;
    }

    public void setClassEntries(List<ClassEntry> classEntries) {
        this.classEntries = classEntries;
    }
}
