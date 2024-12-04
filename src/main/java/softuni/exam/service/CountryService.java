package softuni.exam.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface CountryService {

    boolean areImported();

    String readCountryFileContent() throws IOException;

    String importCountries() throws IOException;
}
