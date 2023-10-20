package artgallery.hsserver.service;

import artgallery.hsserver.dto.PaintingDTO;
import artgallery.hsserver.exception.*;
import artgallery.hsserver.model.*;
import artgallery.hsserver.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaintingService {
  private final PaintingRepository paintingRepository;
  private final ArtistRepository artistRepository;
  @Autowired
  private final GalleryPaintingRepository galleryPaintingRepository;
  private final GalleryRepository galleryRepository;


  public List<PaintingDTO> getAllPaintings(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<PaintingEntity> paintingPage = paintingRepository.findAll(pageable);

    List<PaintingEntity> paintings = paintingPage.getContent();
    return mapToPaintingDtoList(paintings);
  }

  public PaintingDTO getPaintingById(long id) throws PaintingDoesNotExistException{
    PaintingEntity painting = paintingRepository.findById(id)
      .orElseThrow(() -> new PaintingDoesNotExistException(id));
    return mapToPaintingDto(painting);
  }

  public PaintingDTO createPainting(PaintingDTO paintingDTO) throws ArtistDoesNotExistException, GalleryDoesNotExistException {

    List<GalleryPaintingEntity> galleryPaintingEntities = takeGalleryPaintings(paintingDTO);

    PaintingEntity painting = mapToPaintingEntity(paintingDTO, takeArtist(paintingDTO.getArtistId()), galleryPaintingEntities);
    for (Long galleryId : paintingDTO.getGalleriesId()) {
      GalleryEntity g = galleryRepository.findById(galleryId).orElseThrow(() ->
        new GalleryDoesNotExistException(galleryId));
    }
    PaintingEntity createdPainting = paintingRepository.save(painting);

    for (Long galleryId : paintingDTO.getGalleriesId()) {
      GalleryEntity gallery = galleryRepository.findById(galleryId)
        .orElseThrow(() -> new GalleryDoesNotExistException(galleryId));

      GalleryPaintingEntity galleryPainting = new GalleryPaintingEntity();
      galleryPainting.setGallery(gallery);
      galleryPainting.setPainting(createdPainting);
      galleryPainting.setDescription("done from Painting");

      galleryPaintingRepository.save(galleryPainting);
    }
    return mapToPaintingDto(createdPainting);
  }

    @Transactional
    public PaintingDTO updatePainting(long id, PaintingDTO paintingDTO) throws PaintingDoesNotExistException,
    ArtistDoesNotExistException, GalleryDoesNotExistException {
    Optional<PaintingEntity> painting = paintingRepository.findById(id);
    if (painting.isPresent()) {
      PaintingEntity p = painting.get();
      p.setName(paintingDTO.getName());
      p.setYearOfCreation(paintingDTO.getYearOfCreation());
      galleryPaintingRepository.deleteGalleryPaintingEntityByPaintingId(id);
      ArtistEntity ott = artistRepository.findById(paintingDTO.getArtistId()).orElseThrow(() ->
        new ArtistDoesNotExistException(paintingDTO.getArtistId()));

      p.setArtistEntity(ott);
      PaintingEntity newPainting = paintingRepository.save(p);

      for (Long galleryId : paintingDTO.getGalleriesId()) {
        GalleryEntity gallery = galleryRepository.findById(galleryId)
          .orElseThrow(() -> new GalleryDoesNotExistException(galleryId));

        GalleryPaintingEntity galleryPainting = new GalleryPaintingEntity();
        galleryPainting.setGallery(gallery);
        galleryPainting.setPainting(newPainting);
        galleryPainting.setDescription("done from Painting");

        galleryPaintingRepository.save(galleryPainting);
      }

      return mapToPaintingDto(newPainting);
    }
    throw new PaintingDoesNotExistException(id);
  }

  @Transactional
  public void deletePainting(long id) throws PaintingDoesNotExistException, GalleryDoesNotExistException {
    if (paintingRepository.existsById(id)) {
      try {
        galleryPaintingRepository.deleteGalleryPaintingEntityByPaintingId(id);
        paintingRepository.deleteById(id);
      } catch (Exception e) {

        throw new GalleryDoesNotExistException(id);
      }
    } else {
      throw new PaintingDoesNotExistException(id);
    }
  }

  private PaintingDTO mapToPaintingDto(PaintingEntity paintingEntity) {
    List<Long> galleryIds = paintingEntity.getGalleryPaintings().stream()
      .map(GalleryPaintingEntity::getId)
      .collect(Collectors.toList());

    return new PaintingDTO(paintingEntity.getId(), paintingEntity.getName(),
      paintingEntity.getYearOfCreation(), paintingEntity.getArtistEntity().getId(), galleryIds);

  }

  private List<PaintingDTO> mapToPaintingDtoList(List<PaintingEntity> paintings) {
    return paintings.stream().map(this::mapToPaintingDto)
      .collect(Collectors.toList());
  }

  private List<GalleryPaintingEntity> takeGalleryPaintings(PaintingDTO paintingDto){
    List<Long> gpIds = paintingDto.getGalleriesId();
    if (gpIds == null) return List.of();
    return galleryPaintingRepository.findAllById(gpIds);
  }

  private ArtistEntity takeArtist(Long artistId) throws ArtistDoesNotExistException {
    ArtistEntity artistEntity = artistRepository.findById(artistId).orElseThrow(() ->
      new ArtistDoesNotExistException(artistId));
    return artistEntity;
  }

  private PaintingEntity mapToPaintingEntity(PaintingDTO paintingDTO, ArtistEntity artist, List<GalleryPaintingEntity> gpList){
    PaintingEntity paintingEntity = new PaintingEntity();
    paintingEntity.setName(paintingDTO.getName());
    paintingEntity.setYearOfCreation(paintingDTO.getYearOfCreation());
    paintingEntity.setArtistEntity(artist);
    paintingEntity.setGalleryPaintings(gpList);
    return paintingEntity;
  }
}
