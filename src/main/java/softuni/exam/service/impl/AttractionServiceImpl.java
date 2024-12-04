package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.AttractionImportDto;
import softuni.exam.models.entity.Attraction;
import softuni.exam.repository.AttractionRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.AttractionService;
import softuni.exam.util.ValidationUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

//ToDo - Implement all the methods
@Service

public class AttractionServiceImpl implements AttractionService {


    private final AttractionRepository attractionRepository;
    private final static String ATTRACTION_FILE_PATH = "src/main/resources/files/json/attractions.json";
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final CountryRepository countryRepository;


    public AttractionServiceImpl(AttractionRepository attractionRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, CountryRepository countryRepository) {
        this.attractionRepository = attractionRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.countryRepository = countryRepository;
    }


    @Override
    public boolean areImported() {
        return this.attractionRepository.count() > 0;
    }

    @Override
    public String readAttractionsFileContent() throws IOException {
        return Files.readString(Path.of(ATTRACTION_FILE_PATH));
    }

    @Override
    public String importAttractions() throws IOException {
        StringBuilder sb = new StringBuilder();
        AttractionImportDto[] attractionImportDtos = this.gson.fromJson(
                readAttractionsFileContent(),
                AttractionImportDto[].class
        );

        for (AttractionImportDto attractionImportDto : attractionImportDtos) {
            if (!this.validationUtil.isValid(attractionImportDto) ||
                    this.attractionRepository.findByName(attractionImportDto.getName()).isPresent()) {
                sb.append("Invalid attraction").append(System.lineSeparator());
                continue;
            }

            Attraction attraction = this.modelMapper.map(attractionImportDto, Attraction.class);

            attraction.setCountry(this.countryRepository.findById((long) attractionImportDto.getCountry()).get());

            this.attractionRepository.save(attraction);
            sb.append(String.format("Successfully imported attraction %s", attractionImportDto.getName()))
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }


    @Override
    public String exportAttractions() {
        StringBuilder sb = new StringBuilder();

        List<Attraction> attractions = this.attractionRepository
                .findByTypeInAndElevationGreaterThanEqualOrderByNameAscCountryNameAsc(
                        List.of("historical site", "archaeological site"), 300);

        for (Attraction attraction : attractions) {
            String attractionInfo = String.format(
                    "Attraction with ID%d:%n***%s - %s at an altitude of %dm. somewhere in %s.%n",
                    attraction.getId(),
                    attraction.getName(),
                    attraction.getDescription(),
                    attraction.getElevation(),
                    attraction.getCountry().getName()
            );
            sb.append(attractionInfo);
        }

        return sb.toString();
    }


}
