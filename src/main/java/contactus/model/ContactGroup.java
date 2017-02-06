package contactus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ContactGroup {
    private final Integer id;
    private final String name;
}
