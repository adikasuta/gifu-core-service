package com.gifu.coreservice.service;

import com.gifu.coreservice.enumeration.ShippingVendor;
import com.gifu.coreservice.model.dto.ValueTextDto;
import com.gifu.coreservice.repository.CityRepository;
import com.gifu.coreservice.repository.DistrictRepository;
import com.gifu.coreservice.repository.KelurahanRepository;
import com.gifu.coreservice.repository.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdministrativeAreaService {

    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private KelurahanRepository kelurahanRepository;

    public List<ValueTextDto> getShippingVendors(){
        List<ValueTextDto> options = new ArrayList<>();
        for(ShippingVendor vendor : ShippingVendor.values()){
            options.add(new ValueTextDto(vendor.name(),vendor.getText()));
        }
        return options;
    }
    public List<ValueTextDto> getProvinceReference() {
        return provinceRepository.findAll()
                .stream()
                .map(it -> new ValueTextDto(it.getId(), it.getName())).collect(Collectors.toList());
    }
    public List<ValueTextDto> getCityReference(String provinceId) {
        return cityRepository.findByProvinceId(provinceId)
                .stream()
                .map(it -> new ValueTextDto(it.getId(), it.getName())).collect(Collectors.toList());
    }
    public List<ValueTextDto> getDistrictReference(String cityId) {
        return districtRepository.findByCityId(cityId)
                .stream()
                .map(it -> new ValueTextDto(it.getId(), it.getName())).collect(Collectors.toList());
    }
    public List<ValueTextDto> getKelurahanReference(String districtId) {
        return kelurahanRepository.findByDistrictId(districtId)
                .stream()
                .map(it -> new ValueTextDto(it.getId(), it.getName())).collect(Collectors.toList());
    }
}
