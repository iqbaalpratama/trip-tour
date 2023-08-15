package com.iqbaal.triptour.service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.iqbaal.triptour.dto.request.CreateTripRequest;
import com.iqbaal.triptour.dto.request.UpdateTripRequest;
import com.iqbaal.triptour.dto.response.TripResponse;
import com.iqbaal.triptour.entity.Trip;
import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.exception.FileTypeNotValidException;
import com.iqbaal.triptour.exception.ResourceNotFoundException;
import com.iqbaal.triptour.repository.TripRepository;
import com.iqbaal.triptour.service.utils.UploadFile;

import jakarta.transaction.Transactional;

@Service
public class TripService {

    private final List<String> typeImage = Arrays.asList(new String[]{"png", "jpg"});
    private final List<String> typeVideo = Arrays.asList(new String[]{"mp4", "mkv", "avi"});
    private final List<String> typeTnc = Arrays.asList(new String[]{"docx", "doc", "txt", "pdf"});

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UploadFile uploadFile;


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
    public TripResponse create(User user, CreateTripRequest createTripRequest, MultipartFile image, MultipartFile video, MultipartFile tnc) throws FileTypeNotValidException {
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

        uploadFile.saveFile(image);
        uploadFile.saveFile(tnc);
        uploadFile.saveFile(video);

        tripRepository.save(trip);
        return toTripResponse(trip);

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
            if(!uploadFile.deleteFile(trip.getImage())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image can't be edited");
            };
            trip.setImage(image.getOriginalFilename());
            uploadFile.saveFile(image);
        }

        if(Objects.nonNull(video)){
            if(!typeVideo.contains(FilenameUtils.getExtension(video.getOriginalFilename()))){
                throw new FileTypeNotValidException("Video file type is not valid");
            };
            if(!uploadFile.deleteFile(trip.getVideo())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video can't be edited");
            };
            trip.setVideo(video.getOriginalFilename());
            uploadFile.saveFile(video);
        }

        if(Objects.nonNull(tnc)){
            if(!typeTnc.contains(FilenameUtils.getExtension(tnc.getOriginalFilename()))){
                throw new FileTypeNotValidException("TnC file type is not valid");
            };
            if(!uploadFile.deleteFile(trip.getTnc())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TnC can't be edited");
            };
            trip.setTnc(tnc.getOriginalFilename());
            uploadFile.saveFile(tnc);
        }
        trip.setUser(user);
        tripRepository.save(trip);
    }

    @Transactional
    public void delete(User user, String id){
        Trip trip = tripRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip with that id not found"));
        uploadFile.deleteFile(trip.getImage());
        uploadFile.deleteFile(trip.getVideo());
        uploadFile.deleteFile(trip.getTnc());
        tripRepository.delete(trip);
    }


    public Resource getResource(String id, String fileType) throws ResourceNotFoundException{
        Trip trip = tripRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip with that id not found"));
        
        if(!fileType.equals("image") && !fileType.equals("video") && !fileType.equals("tnc")){
                throw new ResourceNotFoundException("Resource type is not found");
        }

        String fileName = fileType.equals("image") ? trip.getImage() : fileType.equals("video") ? trip.getVideo() : fileType.equals("image") ? trip.getTnc() : "";
        Resource resource = uploadFile.loadFileAsResource(fileName);
        return resource;
    }

    private TripResponse toTripResponse(Trip trip){
        return TripResponse.builder()
                .name(trip.getName())
                .city(trip.getCity())
                .price(trip.getPrice())
                .quota(trip.getQuota())
                .noOfDays(trip.getNoOfDays())
                .status(trip.getStatus())
                .imagePath(uploadFile.loadPathFile(trip.getImage()))
                .videoPath(uploadFile.loadPathFile(trip.getVideo()))
                .tncPath(uploadFile.loadPathFile(trip.getTnc()))
                .build();
    }
}
