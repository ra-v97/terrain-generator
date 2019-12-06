using DelimitedFiles
using Images, Colors

dataDir = "./data/srtm/version2_1/SRTM3/Eurasia/"
outputFile = "./out/poland.data"

resolution = 3000

function getFileNames(dir)
    fileNames = [split(f, ".")[1] for f in readdir(dataDir)]
    filter(f -> !startswith(f, "i") && !isempty(f), fileNames)
end

function getFileNamesWithCoords(fileNames)
    filesWithCoords = []
    for f in files
        n = parse(Int8, f[2:3])
        e = parse(Int8, f[5:7])
        push!(filesWithCoords, (f, (n,e)))
    end
    filesWithCoords
end

files = getFileNames(dataDir)
filesWithCoords = getFileNamesWithCoords(files)

polandSquareCoords =[(y,x) for x in range(14, 24, step=1) for y in range(49, 54, step=1)]
filesWithCoords
polandSquareCoordsWithFiles = filter(x -> x[2] in polandSquareCoords, filesWithCoords)
coordsWithFiles = polandSquareCoordsWithFiles

boundaries = (W = minimum(map(x -> x[2][2], coordsWithFiles)),
E = maximum(map(x -> x[2][2], coordsWithFiles)),
N = maximum(map(x -> x[2][1], coordsWithFiles)),
S = minimum(map(x -> x[2][1], coordsWithFiles)))
boundaries

fn = string(dataDir, first(files), ".hgt")
siz = floor(Int32, filesize(fn))
dim = floor(Int32, sqrt(siz/2))
if(dim*dim*2 != siz || siz == 0)
    throw("Bad files")
end

mapArr = Array{Int16,2}(undef, ceil(Int32, (boundaries.N - boundaries.S + 1)*dim),
    ceil(Int32, (boundaries.E - boundaries.W + 1)*dim))

filled=[]
for (file, coords) in coordsWithFiles
    fn = string(dataDir, file, ".hgt")
    io = open(fn, "r")
    data = reshape(reinterpret(Int16, read(io, dim*dim*2)), (dim, dim))
    data = rotl90(data)
    data .= ntoh.(data)
    close(io)
    coordsVector = [coords[2]-boundaries.W, coords[1]-boundaries.S]
    pos = [coordsVector[2]*dim+1, coordsVector[1]*dim+1]
    push!(filled, pos)
    mapArr[pos[1]:pos[1]+dim-1, pos[2]:pos[2]+dim-1] = data
end

mapArr
mapArr = reverse(mapArr, dims=1)
resizedMap = imresize(mapArr, resolution, resolution)
resizedMap = floor.(Int16, resizedMap)

function printArr(array)
    max = 0xffff
    newarr = array .* (255/max)
    Gray.(newarr)
end

printArr(mapArr)
printArr(resizedMap)

fn = open(outputFile, "w")
write(fn, resizedMap)
close(fn)
