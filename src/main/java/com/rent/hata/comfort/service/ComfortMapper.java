package com.rent.hata.comfort.service;

import com.rent.hata.comfort.dto.Comfort;
import com.rent.hata.comfort.entity.ComfortEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ComfortMapper {

    public Comfort toDomainComfort(ComfortEntity comfort){
        ComfortValidator.checkNotNull(comfort);
        return new Comfort(
          comfort.getId(),
          comfort.getTittle(),
          comfort.getIcon(),
          comfort.getDescription()
        );
    }

    public ComfortEntity toEntity(Comfort comfort){
        ComfortValidator.checkNotNull(comfort);
        return new ComfortEntity(
           comfort.id(),
           comfort.title(),
           comfort.icon(),
           comfort.description()
        );
    }

    public ComfortEntity toEntity(ComfortEntity comfortEntity, Comfort comfort){
        Optional.ofNullable(comfort.title()).ifPresent(comfortEntity::setTittle);
        Optional.ofNullable(comfort.icon()).ifPresent(comfortEntity::setIcon);
        Optional.ofNullable(comfort.description()).ifPresent(comfortEntity::setDescription);
        return comfortEntity;
    }
}
