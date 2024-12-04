package softuni.exam.models.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "personal_datas")
public class PersonalDataImportRootDto {

    private List<PersonalDataImportDto> personalData;

    @XmlElement(name = "personal_data")
    public List<PersonalDataImportDto> getPersonalData() {
        return personalData;
    }

    public void setPersonalData(List<PersonalDataImportDto> personalData) {
        this.personalData = personalData;
    }
}
