package softuni.exam.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface AttractionService {

    boolean areImported();

    String readAttractionsFileContent() throws IOException;

    String importAttractions() throws IOException;

    String exportAttractions();

}
