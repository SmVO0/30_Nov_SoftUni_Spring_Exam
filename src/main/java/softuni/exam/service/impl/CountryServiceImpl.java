package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CountryImportDto;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final static String COUNTRIES_FILE_PATH = "src/main/resources/files/json/countries.json";
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public CountryServiceImpl(CountryRepository countryRepository, ModelMapper modelMapper, ModelMapper modelMapper1, ValidationUtil validationUtil, Gson gson) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }


    @Override
    public boolean areImported() {
        return this.countryRepository.count() > 0;
    }

    @Override
    public String readCountryFileContent() throws IOException {
        return Files.readString(Path.of(COUNTRIES_FILE_PATH));
    }

    @Override
    public String importCountries() throws IOException {
        StringBuilder sb = new StringBuilder();

        CountryImportDto[] countryImportDtos = this.gson.fromJson(readCountryFileContent(), CountryImportDto[].class);

        for (CountryImportDto countryImportDto : countryImportDtos) {
            if (!this.validationUtil.isValid(countryImportDto) ||
                    this.countryRepository.findByName(countryImportDto.getName()).isPresent()) {
                sb.append("Invalid country").append(System.lineSeparator());
                continue;
            }

            try {
                Country country = this.modelMapper.map(countryImportDto, Country.class);
                this.countryRepository.save(country);

                sb.append(String.format("Successfully imported country %s", countryImportDto.getName()))
                        .append(System.lineSeparator());
            } catch (Exception e) {
                sb.append("Error while importing country: ").append(countryImportDto.getName())
                        .append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

}
