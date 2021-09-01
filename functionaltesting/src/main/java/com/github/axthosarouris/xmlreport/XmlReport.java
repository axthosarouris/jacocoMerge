package com.github.axthosarouris.xmlreport;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlReport {

    @XmlElement(name = "package")
    private List<PackageEntry> packageEntries;

    public List<PackageEntry> getPackageEntries() {
        return packageEntries;
    }

    @XmlTransient
    public Set<String> getClassNames(){
        return this.getPackageEntries()
            .stream()
            .flatMap(pack->pack.getClassEntries().stream())
            .map(ClassEntry::getName)
            .collect(Collectors.toSet());
    }

    public  List<String> listAllReportedMethodNames() {
        return getPackageEntries().stream()
            .map(PackageEntry::getClassEntries)
            .flatMap(Collection::stream)
            .map(ClassEntry::getMethodEntries)
            .flatMap(Collection::stream)
            .map(MethodEntry::getName)
            .collect(Collectors.toList());
    }
}
