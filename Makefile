
build: src/main/scala/axi/Axi.scala src/main/scala/axi/axi_module.scala
	sbt 'runMain axi.axi_module_obj --target-dir build'

clean:
	rm -rf ./build
