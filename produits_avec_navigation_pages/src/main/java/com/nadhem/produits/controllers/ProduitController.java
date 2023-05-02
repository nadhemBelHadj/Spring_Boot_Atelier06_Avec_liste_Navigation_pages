package com.nadhem.produits.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nadhem.produits.entities.Categorie;
import com.nadhem.produits.entities.Produit;
import com.nadhem.produits.service.ProduitService;

@Controller
public class ProduitController {
	@Autowired
	ProduitService produitService;
	
	
	@RequestMapping("/showCreate")
	public String showCreate(ModelMap modelMap)
	{
		List<Categorie> cats = produitService.getAllCategories();
		Produit prod = new Produit();
		Categorie cat = new Categorie();
		cat = cats.get(0); // prendre la première catégorie de la liste
		prod.setCategorie(cat); //affedter une catégorie par défaut au produit pour éviter le pb avec une catégorie null
		modelMap.addAttribute("produit",prod);
		modelMap.addAttribute("mode", "new");
		modelMap.addAttribute("categories", cats);
		modelMap.addAttribute("page",0);
		return "formProduit";
	}
	
	@RequestMapping("/saveProduit")
	public String saveProduit(@Valid Produit produit,
							  BindingResult bindingResult,
							  @ModelAttribute("page") int pageFromPrevious,
							  @RequestParam (name="size", defaultValue = "4") int size,
							  RedirectAttributes redirectAttributes,
							  ModelMap modelMap
							  )
	{
		
		  int page;
		    if (bindingResult.hasErrors()) 
		    {
		    	List<Categorie> cats = produitService.getAllCategories();
		    	modelMap.addAttribute("categories", cats);
		    	//même on est en mode ajout (mode="new"), en passe le mode edit pour garder la catégorie 
		    	//selectionnée dans la liste, pour mieux le comprendre essayer de passer le mode "new"
		    	 modelMap.addAttribute("mode", "edit");
				return "formProduit";
		    }
		   
		    if (produit.getIdProduit()==null) //adding
		        page = produitService.getAllProduits().size()/size; // calculer le nbr de pages
		    else //updating
		        page = pageFromPrevious;		  
		 
		    produitService.saveProduit(produit);
		    
		    redirectAttributes.addAttribute("page", page);
		    return "redirect:/ListeProduits";
	}
	
	@RequestMapping("/ListeProduits")
	public String listeProduits(ModelMap modelMap,
			@RequestParam (name="page",defaultValue = "0") int page,
			@RequestParam (name="size", defaultValue = "4") int size)
	{
		Page<Produit> prods = produitService.getAllProduitsParPage(page, size);
		modelMap.addAttribute("produits", prods);		
		modelMap.addAttribute("pages", new int[prods.getTotalPages()]);	
		modelMap.addAttribute("currentPage", page);	
		return "listeProduits";	
	}

	@RequestMapping("/supprimerProduit")
	public String supprimerProduit(@RequestParam("id") Long id,
			ModelMap modelMap,
			@RequestParam (name="page",defaultValue = "0") int page,
			@RequestParam (name="size", defaultValue = "4") int size)
	{
		produitService.deleteProduitById(id);
		Page<Produit> prods = produitService.getAllProduitsParPage(page, size);
		modelMap.addAttribute("produits", prods);		
		modelMap.addAttribute("pages", new int[prods.getTotalPages()]);	
		modelMap.addAttribute("currentPage", page);	
		modelMap.addAttribute("size", size);	
		return "listeProduits";	
	}
	
	@RequestMapping("/modifierProduit")
	public String editerProduit(@RequestParam("id") Long id,
			   					@RequestParam("page") int page,
			   					ModelMap modelMap)
	{
		Produit p= 	produitService.getProduit(id);
		List<Categorie> cats = produitService.getAllCategories();
		modelMap.addAttribute("produit", p);
		modelMap.addAttribute("mode", "edit");
		modelMap.addAttribute("categories", cats);
		modelMap.addAttribute("page",page);
		return "formProduit";	
	}

	/*
	 * @RequestMapping("/updateProduit") public String
	 * updateProduit(@ModelAttribute("produit") Produit produit,ModelMap modelMap) {
	 * produitService.updateProduit(produit); List<Produit> prods =
	 * produitService.getAllProduits(); modelMap.addAttribute("produits", prods);
	 * 
	 * return "listeProduits"; }
	 */



}
