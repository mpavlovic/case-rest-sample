/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hr.foi.mtlab.data;

/**
 * Klasa predstavlja jednu zemljopisnu adresu 
 * s kojom se radi u aplikacijama.
 * @author dkermek
 */
public class Adresa {
    private long idadresa;
    private String adresa;
    private Lokacija geoloc;

    /**
     * Konstruktor.
     */
    public Adresa() {
    }

    /**
     * Konstruktor.
     * @param idadresa ID broj adrese
     * @param adresa String zapis adrese
     * @param geoloc Lokacija adrese (zemljopisne koordinate)
     */
    public Adresa(long idadresa, String adresa, Lokacija geoloc) {
        this.idadresa = idadresa;
        this.adresa = adresa;
        this.geoloc = geoloc;
    }

    /**
     * Dohvaća objekt koji sadrži informacije o 
     * zemljopisnim koordinatama adrese.
     * @return objekt tipa Lokacija
     */
    public Lokacija getGeoloc() {
        return geoloc;
    }

    /**
     * Postavlja lokaciju adrese (zemljopisne koordinate).
     * @param geoloc objekt tipa Lokacija
     */
    public void setGeoloc(Lokacija geoloc) {
        this.geoloc = geoloc;
    }

    /**
     * Dohvaća ID broj adrese.
     * @return long ID broj adrese
     */
    public long getIdadresa() {
        return idadresa;
    }

    /**
     * Postavlja ID broj adrese.
     * @param idadresa željeni ID long broj adrese
     */
    public void setIdadresa(long idadresa) {
        this.idadresa = idadresa;
    }

    /**
     * Dohvaća tekstualni zapis adrese.
     * @return String adrese
     */
    public String getAdresa() {
        return adresa;
    }

    /**
     * Postavlja tekstualni zapis adrese.
     * @param adresa String adrese.
     */
    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }
    
}
