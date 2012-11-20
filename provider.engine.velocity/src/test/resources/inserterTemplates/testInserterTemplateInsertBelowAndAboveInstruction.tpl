@TargetFileName testInserterTargetFile.txt # Name of file with extension without path
@TargetDir temp
@InsertBelow InsertBelow
@InsertAbove InsertAbove

#foreach($classDescriptor in $model.classDescriptorList)

'	${classDescriptor.simpleName}

#end
