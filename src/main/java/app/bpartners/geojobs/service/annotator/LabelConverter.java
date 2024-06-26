package app.bpartners.geojobs.service.annotator;

import static java.util.UUID.randomUUID;

import app.bpartners.gen.annotator.endpoint.rest.model.Label;
import app.bpartners.geojobs.repository.model.detection.DetectableType;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class LabelConverter implements Function<DetectableType, Label> {

  @Override
  public Label apply(DetectableType detectableType) {
    return new Label()
        .id(randomUUID().toString())
        .color(getColorFromDetectedType(detectableType))
        .name(detectableType.name());
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
}
