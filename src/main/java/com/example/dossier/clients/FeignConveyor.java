package com.example.dossier.clients;



import com.example.dossier.dto.Application;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "feignDeal", url = "http://deal:8079/deal/")
public interface FeignConveyor {


    @PutMapping("/admin/application/{applicationId}/status")
    ResponseEntity<Application> updateStatus(@PathVariable(name = "applicationId") Long applicationId);

    @GetMapping("/admin/application/{applicationId}")
    ResponseEntity<Application> getApplication(@PathVariable(name = "applicationId") Long applicationId);
}


