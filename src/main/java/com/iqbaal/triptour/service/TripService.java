package com.iqbaal.triptour.service;

import com.iqbaal.triptour.entity.Trip;
import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.exception.FileTypeNotValidException;
import com.iqbaal.triptour.model.request.CreateTripRequest;
import com.iqbaal.triptour.model.request.UpdateTripRequest;
import com.iqbaal.triptour.model.response.TripResponse;
import com.iqbaal.triptour.model.response.UserResponse;
import com.iqbaal.triptour.repository.TripRepository;
import com.iqbaal.triptour.security.BCrypt;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TripService {

    private final Path root = Paths.get("uploads");
    private final List<String> typeImage = Arrays.asList(new String[]{"png", "jpg"});
    private final List<String> typeVideo = Arrays.asList(new String[]{"mp4", "mkv", "avi"});
    private final List<String> typeTnc = Arrays.asList(new String[]{"docx", "doc", "txt", "pdf"});

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ValidationService validationService;

    public void initUpload() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public List<TripResponse> getAllTrips(){
        List<Trip> trips = tripRepository.findAll();
        return trips.stream().map(this::toTripResponse).toList();
    }

    public TripResponse getById(String id){
        Trip trip = tripRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip with that Id not found"));
        return toTripResponse(trip);
    }

    @Transactional
    public void create(User user, CreateTripRequest createTripRequest, MultipartFile image, MultipartFile video, MultipartFile tnc) throws FileTypeNotValidException {
        validationService.validate(createTripRequest);
        if(!typeImage.contains(FilenameUtils.getExtension(image.getOriginalFilename()))){
            throw new FileTypeNotValidException("Image file type is not valid");
        };
        if(!typeVideo.contains(FilenameUtils.getExtension(video.getOriginalFilename()))){
            throw new FileTypeNotValidException("Video file type is not valid");
        };
        if(!typeTnc.contains(FilenameUtils.getExtension(tnc.getOriginalFilename()))){
            throw new FileTypeNotValidException("TnC file type is not valid");
        };

        Trip trip = new Trip();
        trip.setName(createTripRequest.getName());
        trip.setCity(createTripRequest.getCity());
        trip.setPrice(createTripRequest.getPrice());
        trip.setNoOfDays(createTripRequest.getNoOfDays());
        trip.setQuota(createTripRequest.getQuota());
        trip.setCreatedDate(ZonedDateTime.now());
        trip.setUser(user);
        trip.setImage(image.getOriginalFilename());
        trip.setVideo(video.getOriginalFilename());
        trip.setTnc(tnc.getOriginalFilename());
        trip.setStatus(createTripRequest.getStatus());

        saveFile(image);
        saveFile(tnc);
        saveFile(video);

        tripRepository.save(trip);

    }

    @Transactional
    public void update(String id, User user, UpdateTripRequest updateTripRequest, MultipartFile image, MultipartFile video, MultipartFile tnc) throws FileTypeNotValidException {
        validationService.validate(updateTripRequest);

        Trip trip = tripRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip with that id not found"));

        if(Objects.nonNull(updateTripRequest.getName())){
            trip.setName(updateTripRequest.getName());
        }
        if(Objects.nonNull(updateTripRequest.getCity())){
            trip.setCity(updateTripRequest.getCity());
        }
        if(Objects.nonNull(updateTripRequest.getPrice())){
            trip.setPrice(updateTripRequest.getPrice());
        }
        if(Objects.nonNull(updateTripRequest.getQuota())){
            trip.setQuota(updateTripRequest.getQuota());
        }
        if(Objects.nonNull(updateTripRequest.getNoOfDays())){
            trip.setNoOfDays(updateTripRequest.getNoOfDays());
        }
        if(Objects.nonNull(updateTripRequest.getStatus())){
            trip.setStatus(updateTripRequest.getStatus());
        }

        if(Objects.nonNull(image)){
            if(!typeImage.contains(FilenameUtils.getExtension(image.getOriginalFilename()))){
                throw new FileTypeNotValidException("Image file type is not valid");
            };
            if(!deleteFile(trip.getImage())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image can't be edited");
            };
            trip.setImage(image.getOriginalFilename());
            saveFile(image);
        }

        if(Objects.nonNull(video)){
            if(!typeVideo.contains(FilenameUtils.getExtension(video.getOriginalFilename()))){
                throw new FileTypeNotValidException("Video file type is not valid");
            };
            if(!deleteFile(trip.getVideo())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video can't be edited");
            };
            trip.setVideo(video.getOriginalFilename());
            saveFile(video);
        }

        if(Objects.nonNull(tnc)){
            if(!typeTnc.contains(FilenameUtils.getExtension(tnc.getOriginalFilename()))){
                throw new FileTypeNotValidException("TnC file type is not valid");
            };
            if(!deleteFile(trip.getTnc())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TnC can't be edited");
            };
            trip.setTnc(tnc.getOriginalFilename());
            saveFile(tnc);
        }
        trip.setUser(user);
        tripRepository.save(trip);
    }

    @Transactional
    public void delete(User user, String id){
        Trip trip = tripRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip with that id not found"));
        deleteFile(trip.getImage());
        deleteFile(trip.getVideo());
        deleteFile(trip.getTnc());
        tripRepository.delete(trip);
    }


    private void saveFile(MultipartFile file){
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    };

    private boolean deleteFile(String filename) {
        try {
            Path file = root.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private TripResponse toTripResponse(Trip trip){
        return TripResponse.builder()
                .name(trip.getName())
                .city(trip.getCity())
                .price(trip.getPrice())
                .quota(trip.getQuota())
                .noOfDays(trip.getNoOfDays())
                .status(trip.getStatus())
                .imagePath(loadPathFile(trip.getImage()))
                .videoPath(loadPathFile(trip.getVideo()))
                .tncPath(loadPathFile(trip.getTnc()))
                .build();
    }

    private String loadPathFile(String filename) {
        if(filename == "" || filename == null){
            return "";
        }
        try {
            Path file = root.resolve(filename);
            return  file.toAbsolutePath().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
