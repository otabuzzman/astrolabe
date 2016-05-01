
package astrolabe;

public interface Atlas extends AuxiliaryEmitter {
	public void addAllAtlasPage() ;
	public astrolabe.model.Chart[] toModel() ;
}
