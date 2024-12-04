package softuni.exam.service;

import java.io.IOException;
import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Service;

@Service
public interface PersonalDataService {

    boolean areImported();

    String readPersonalDataFileContent() throws IOException, JAXBException;

    String importPersonalData() throws IOException, JAXBException;
}
