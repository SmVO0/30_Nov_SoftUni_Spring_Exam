package softuni.exam.service.impl;

import jakarta.xml.bind.JAXBException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.VisitorImportDto;
import softuni.exam.models.dto.VisitorImportRootDto;
import softuni.exam.models.entity.Visitor;
import softuni.exam.models.entity.PersonalData;
import softuni.exam.models.entity.Attraction;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.VisitorRepository;
import softuni.exam.repository.PersonalDataRepository;
import softuni.exam.repository.AttractionRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.VisitorService;
import softuni.exam.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class VisitorServiceImpl implements VisitorService {

    private static final String VISITORS_FILE_PATH = "src/main/resources/files/xml/visitors.xml";
    private final VisitorRepository visitorRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final PersonalDataRepository personalDataRepository;
    private final AttractionRepository attractionRepository;
    private final CountryRepository countryRepository;

    public VisitorServiceImpl(VisitorRepository visitorRepository, ModelMapper modelMapper, XmlParser xmlParser,
                              PersonalDataRepository personalDataRepository, AttractionRepository attractionRepository,
                              CountryRepository countryRepository) {
        this.visitorRepository = visitorRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.personalDataRepository = personalDataRepository;
        this.attractionRepository = attractionRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public boolean areImported() {
        return this.visitorRepository.count() > 0;
    }

    @Override
    public String readVisitorsFileContent() throws IOException {
        return Files.readString(Path.of(VISITORS_FILE_PATH));
    }

    @Override
    public String importVisitors() throws JAXBException {
        VisitorImportRootDto visitorImportRootDto = xmlParser.fromFile(VISITORS_FILE_PATH, VisitorImportRootDto.class);

        StringBuilder result = new StringBuilder();

        for (VisitorImportDto visitorImportDto : visitorImportRootDto.getVisitors()) {

            Visitor existingVisitor = visitorRepository.findByFirstNameAndLastNameAndPersonalDataId(
                    visitorImportDto.getFirstName(),
                    visitorImportDto.getLastName(),
                    visitorImportDto.getPersonalDataId()
            );
            if (existingVisitor != null) {
                result.append("Invalid visitor").append(System.lineSeparator());
                continue;
            }

            Visitor visitor = modelMapper.map(visitorImportDto, Visitor.class);

            PersonalData personalData = personalDataRepository.findById(visitorImportDto.getPersonalDataId()).orElse(null);
            visitor.setPersonalData(personalData);

            Attraction attraction = attractionRepository.findById(visitorImportDto.getAttractionId()).orElse(null);
            visitor.setAttraction(attraction);

            Country country = countryRepository.findById(visitorImportDto.getCountryId()).orElse(null);
            visitor.setCountry(country);

            visitorRepository.save(visitor);
            result.append(String.format("Successfully imported visitor %s %s", visitor.getFirstName(), visitor.getLastName()))
                    .append(System.lineSeparator());
        }

        return result.toString();
    }
}
