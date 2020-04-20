package software.amazon.imagebuilder.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallbackContext {
    @Builder.Default
    private boolean imageCreationInvoked = false;
}
