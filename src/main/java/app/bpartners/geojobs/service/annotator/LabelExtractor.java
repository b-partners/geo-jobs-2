package app.bpartners.geojobs.service.annotator;

import static app.bpartners.geojobs.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;

import app.bpartners.gen.annotator.endpoint.rest.model.*;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.model.detection.DetectableType;
import app.bpartners.geojobs.repository.model.detection.DetectedObject;
import app.bpartners.geojobs.repository.model.detection.DetectedTile;
import app.bpartners.geojobs.service.KeyPredicateFunction;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LabelExtractor implements Function<DetectableType, Label> {
  private final KeyPredicateFunction keyPredicateFunction;

  @Override
  public Label apply(DetectableType detectableType) {
    return new Label()
        .id(randomUUID().toString())
        .color(getColorFromDetectedType(detectableType))
        .name(detectableType.name());
  }

  public List<Label> createUniqueLabelListFrom(List<DetectedTile> tiles) {
    return tiles.stream()
        .map(DetectedTile::getDetectedObjects)
        .flatMap(Collection::stream)
        .map(DetectedObject::getDetectableObjectType)
        .distinct()
        .map(this)
        .toList();
  }

  public Label findLabelByNameFromList(List<Label> source, String labelName) {
    return source.stream()
        .filter(
            label -> {
              assert label.getName() != null;
              return label.getName().equals(labelName);
            })
        .findFirst()
        .orElseThrow(
            () ->
                new ApiException(
                    SERVER_EXCEPTION,
                    "could not find label " + labelName + " for the current job."));
  }

  private static String getColorFromDetectedType(DetectableType detectableType) {
    return switch (detectableType) {
      case ROOF -> "#DFFF00";
      case SOLAR_PANEL -> "#0E4EB3";
      case POOL -> "#0DCBD2";
      case PATHWAY -> "#F5F586";
      case TREE -> "#4BFF33";
      default -> throw new IllegalArgumentException("unexpected value");
    };
  }

  public List<Label> extractLabelsFromTasks(List<CreateAnnotatedTask> annotatedTasks) {
    return annotatedTasks.stream()
        .map(CreateAnnotatedTask::getAnnotationBatch)
        .map(CreateAnnotationBatch::getAnnotations)
        .map(a -> a.stream().map(AnnotationBaseFields::getLabel).collect(toSet()))
        .reduce(
            new HashSet<>(),
            (acc, val) -> {
              acc.addAll(val);
              return acc;
            })
        .stream()
        .filter(keyPredicateFunction.apply(Label::getName))
        .toList();
  }
}
