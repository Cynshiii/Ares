package co.aegis.ares.framework.annotations;

import co.aegis.ares.utils.JsonBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.ComponentLike;

@Data
@NoArgsConstructor
public class AresException extends RuntimeException {
	private JsonBuilder json;

	public AresException(JsonBuilder json) {
		super(json.toString());
		this.json = json;
	}

	public AresException(ComponentLike component) {
		this(new JsonBuilder(component));
	}

	public AresException(String message) {
		this(new JsonBuilder(message));
	}

//	public ComponentLike withPrefix(String prefix) {
//		return new JsonBuilder(prefix).next(getJson());
//	}

}
