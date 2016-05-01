
package astrolabe;

import org.exolab.castor.xml.ValidationException;

public interface Atlas extends AuxiliaryEmitter {
	public void addAllAtlasPage() throws ValidationException ;
	public astrolabe.model.Chart[] toModel() throws ValidationException ;
}
