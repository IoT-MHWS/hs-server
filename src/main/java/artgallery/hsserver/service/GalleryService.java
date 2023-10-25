package artgallery.hsserver.service;

import artgallery.hsserver.dto.GalleryDTO;
import artgallery.hsserver.exception.GalleryDoesNotExistException;
import artgallery.hsserver.exception.PaintingDoesNotExistException;
import artgallery.hsserver.model.GalleryEntity;
import artgallery.hsserver.model.GalleryPaintingEntity;
import artgallery.hsserver.model.PaintingEntity;
import artgallery.hsserver.repository.GalleryPaintingRepository;
import artgallery.hsserver.repository.GalleryRepository;
import artgallery.hsserver.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
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
public class GalleryService {
  private final GalleryRepository galleryRepository;
  private final GalleryPaintingRepository galleryPaintingRepository;
  private final PaintingRepository paintingRepository;

  public List<GalleryDTO> getAllGalleries(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<GalleryEntity> galleryPage = galleryRepository.findAll(pageable);

    List<GalleryEntity> galleries = galleryPage.getContent();
    return mapToGalleryDtoList(galleries);
  }


  public GalleryDTO getGalleryById(long id) throws GalleryDoesNotExistException{
    GalleryEntity optionalGallery = galleryRepository.findById(id)
      .orElseThrow(() -> new GalleryDoesNotExistException(id));
    return mapToGalleryDto(optionalGallery);

  }

  @Transactional
  public GalleryDTO createGallery(GalleryDTO galleryDto) throws PaintingDoesNotExistException {
    List<GalleryPaintingEntity> galleryPaintingEntities = takeGalleryPaintings(galleryDto);
    GalleryEntity gallery = mapToGalleryEntity(galleryDto, galleryPaintingEntities);
    galleryRepository.save(gallery);

    for (Long paintingId : galleryDto.getPaintingsId()) {
      PaintingEntity painting = paintingRepository.findById(paintingId)
        .orElseThrow(() -> new PaintingDoesNotExistException(paintingId));

      GalleryPaintingEntity galleryPainting = new GalleryPaintingEntity();
      galleryPainting.setGallery(gallery);
      galleryPainting.setPainting(painting);
      galleryPainting.setDescription("done from Gallery");

      galleryPaintingRepository.save(galleryPainting);
    }

    return mapToGalleryDto(gallery);
  }

  @Transactional
  public GalleryDTO updateGallery(long id, GalleryDTO galleryDto) throws GalleryDoesNotExistException, PaintingDoesNotExistException {
    Optional<GalleryEntity> optionalGallery = galleryRepository.findById(id);
    if (optionalGallery.isPresent()) {
      GalleryEntity existingGallery = optionalGallery.get();
      existingGallery.setName(galleryDto.getName());
      existingGallery.setAddress(galleryDto.getAddress());
      galleryPaintingRepository.deleteGalleryPaintingEntityByGalleryId(id);
      GalleryEntity updatedGallery = galleryRepository.save(existingGallery);
      for (Long paintingId : galleryDto.getPaintingsId()) {
        PaintingEntity painting = paintingRepository.findById(paintingId)
          .orElseThrow(() -> new PaintingDoesNotExistException(paintingId));

        GalleryPaintingEntity galleryPainting = new GalleryPaintingEntity();
        galleryPainting.setPainting(painting);
        galleryPainting.setGallery(existingGallery);
        galleryPainting.setDescription("done from Gallery");

        galleryPaintingRepository.save(galleryPainting);
      }
      return mapToGalleryDto(updatedGallery);
    }
    throw new GalleryDoesNotExistException(id);
  }


  @Transactional
  public void deleteGallery(long id) {
    if (galleryPaintingRepository.existsByGalleryId(id)) {
      galleryPaintingRepository.deleteGalleryPaintingEntityByGalleryId(id);
    }
    if (galleryRepository.existsById(id)) {
      galleryRepository.deleteById(id);
    }
  }

  private GalleryDTO mapToGalleryDto(GalleryEntity gallery) {
   List<Long> paintIds = gallery.getGalleryPaintings().stream()
      .map(GalleryPaintingEntity::getId)
      .collect(Collectors.toList());
    return new GalleryDTO(gallery.getId(), gallery.getName(), gallery.getAddress(), paintIds);
  }

  private List<GalleryDTO> mapToGalleryDtoList(List<GalleryEntity> galleries) {
    return galleries.stream().map(this::mapToGalleryDto)
      .collect(Collectors.toList());
  }

  private List<GalleryPaintingEntity> takeGalleryPaintings(GalleryDTO galleryDto){
    List<Long> gpIds = galleryDto.getPaintingsId();
    List<GalleryPaintingEntity> galleryPaintingEntities = galleryPaintingRepository.findAllById(gpIds);
    return galleryPaintingEntities;
  }

  private GalleryEntity mapToGalleryEntity(GalleryDTO galleryDto, List<GalleryPaintingEntity> gpList) {
    GalleryEntity gallery = new GalleryEntity();
    gallery.setName(galleryDto.getName());
    gallery.setAddress(galleryDto.getAddress());
    gallery.setGalleryPaintings(gpList);
    return gallery;
  }
}
