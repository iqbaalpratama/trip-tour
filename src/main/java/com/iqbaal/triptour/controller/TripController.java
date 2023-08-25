package com.iqbaal.triptour.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.iqbaal.triptour.dto.request.CreateTripRequest;
import com.iqbaal.triptour.dto.request.UpdateTripRequest;
import com.iqbaal.triptour.dto.response.TripResponse;
import com.iqbaal.triptour.dto.response.WebResponse;
import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.exception.FileTypeNotValidException;
import com.iqbaal.triptour.exception.ResourceNotFoundException;
import com.iqbaal.triptour.service.TripService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/trips")
public class TripController {

        private final TripService tripService;

        @GetMapping(
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYER')")
        public WebResponse<List<TripResponse>> getAllTrips(){
        List<TripResponse> tripResponses = tripService.getAllTrips();
        return WebResponse.<List<TripResponse>>builder().data(tripResponses).build();
        }

        @GetMapping(
                path = "/{id}",
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYER')")
        public WebResponse<TripResponse> getTripById(@PathVariable String id){
        TripResponse tripResponse = tripService.getById(id);
        return WebResponse.<TripResponse>builder().data(tripResponse).build();
        }


        @PostMapping(
                path = "/create",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        @PreAuthorize("hasRole('EMPLOYER')")
        public WebResponse<TripResponse> create(
                User user,
                @RequestPart(value = "image", required = true) MultipartFile image,
                @RequestPart(value = "video", required = true) MultipartFile video,
                @RequestPart(value = "tnc", required = true) MultipartFile tnc,
                @RequestPart CreateTripRequest createTripRequest
        ) throws FileTypeNotValidException {
        TripResponse tripResponse = tripService.create(user,createTripRequest, image, video, tnc);
        return WebResponse.<TripResponse>builder().data(tripResponse).build();
        }

        @PatchMapping(
                path = "/update/{id}",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        @PreAuthorize("hasRole('EMPLOYER')")
        public WebResponse<String> update(
                User user,
                @PathVariable String id,
                @RequestPart(value = "image", required = false) MultipartFile image,
                @RequestPart(value = "video", required = false) MultipartFile video,
                @RequestPart(value = "tnc", required = false) MultipartFile tnc,
                @RequestPart UpdateTripRequest updateTripRequest
        ) throws FileTypeNotValidException {
        tripService.update(id, user,updateTripRequest, image, video, tnc);
        return WebResponse.<String>builder().data("OK").build();
        }

        @DeleteMapping(
                path = "/delete/{id}",
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        @PreAuthorize("hasRole('EMPLOYER')")
        public WebResponse<String> delete(
                User user,
                @PathVariable String id
        ){
        tripService.delete(user,id);
        return WebResponse.<String>builder().data("OK").build();
        }

        @GetMapping(
                path = "/{id}/{fileType}"
        )
        public ResponseEntity<Resource> getFile(
                User user, 
                @PathVariable("id") String id, 
                @PathVariable("fileType") String fileType
        ) throws ResourceNotFoundException {
                System.out.println(fileType);
                Resource file = tripService.getResource(id, fileType);
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
        }
}
