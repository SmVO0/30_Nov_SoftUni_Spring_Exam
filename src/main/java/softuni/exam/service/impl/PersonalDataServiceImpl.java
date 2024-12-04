package softuni.exam.service.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PersonalDataImportDto;
import softuni.exam.models.dto.PersonalDataImportRootDto;
import softuni.exam.models.entity.PersonalData;
import softuni.exam.repository.PersonalDataRepository;
import softuni.exam.service.PersonalDataService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//ToDo - Implement all the methods

@Service
public class PersonalDataServiceImpl implements PersonalDataService {

    private final PersonalDataRepository personalDataRepository;
    private final static String PERSONAL_DATA_FILE_PATH = "src/main/resources/files/xml/personal_data.xml";
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public PersonalDataServiceImpl(PersonalDataRepository personalDataRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.personalDataRepository = personalDataRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }


    @Override
    public boolean areImported() {
        return this.personalDataRepository.count() > 0;
    }

    @Override
    public String readPersonalDataFileContent() throws IOException {
        return Files.readString(Path.of(PERSONAL_DATA_FILE_PATH));
    }

    @Override
    public String importPersonalData() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        PersonalDataImportRootDto personalDataImportRootDto = xmlParser.fromFile(PERSONAL_DATA_FILE_PATH, PersonalDataImportRootDto.class);

        for (PersonalDataImportDto personalDataImportDto : personalDataImportRootDto.getPersonalData()) {

            if (!validationUtil.isValid(personalDataImportDto) ||
                    personalDataRepository.existsByCardNumber(personalDataImportDto.getCardNumber())) {
                sb.append("Invalid personal data").append(System.lineSeparator());
                continue;
            }

            PersonalData personalData = modelMapper.map(personalDataImportDto, PersonalData.class);
            personalDataRepository.saveAndFlush(personalData); // Save the entity

            sb.append("Successfully imported personal data for visitor with card number ")
                    .append(personalDataImportDto.getCardNumber())
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

}
