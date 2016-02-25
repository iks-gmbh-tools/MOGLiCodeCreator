@targetFileName ShoppingCart.xml # Name of output file with extension but without path 
@targetDir <applicationRootDir>
@createNew true
@NameOfValidModel XMLBuilder

#set( $prefix = $model.getMetaInfoValueFor("nameSpacePrefix") )
#set( $xmlns = $model.getMetaInfoValueFor("xmlns") )

<?xml version="1.0" encoding="UTF-8"?>
<$prefix:shoppingCart $xmlns>
	<$prefix:shoppingCartVersion>1.0</$prefix:shoppingCartVersion>
	
	# the customer data defined in the model file will be inserted above the <boughItemList> line.

	<$prefix:boughItemList>
		# the customer data defined in the model file will be inserted below the <boughItemList> line.
	</$prefix:boughItemList>

	# the following section (lines between </boughItemList> and </shoppingCart>) will be completely replaced by data read from the model file 
	<$prefix:AdditionalInfo>
		<Advertising info="There are always special offers for you. Ask for more information!" />
	</$prefix:AdditionalInfo>
	
</$prefix:shoppingCart>

# Note that the comment lines in template files will not enter the generated output!