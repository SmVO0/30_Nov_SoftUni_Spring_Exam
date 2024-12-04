package softuni.exam.models.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "visitors")
public class VisitorImportRootDto {

    @XmlElementWrapper(name = "visitors")
    @XmlElement(name = "visitor")
    private List<VisitorImportDto> visitors;

    public List<VisitorImportDto> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<VisitorImportDto> visitors) {
        this.visitors = visitors;
    }
}
